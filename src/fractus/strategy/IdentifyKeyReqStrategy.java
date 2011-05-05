package fractus.strategy;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.math.ec.ECPoint;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import fractus.main.BinaryUtil;
import fractus.main.FractusMessage;
import fractus.main.UserTracker;
import fractus.net.ConnectorContext;
import fractus.net.FractusConnector;
import fractus.net.ProtocolBuffer;
import fractus.net.ProtocolBuffer.IdentifyKeyRes.ResponseCode;

public class IdentifyKeyReqStrategy implements PacketStrategy {

    private final static Logger log =
    	Logger.getLogger(IdentifyKeyReqStrategy.class.getName());	
	private UserTracker userTracker;
	private ConnectorContext connectorContext;
	
	public IdentifyKeyReqStrategy(ConnectorContext connectorContext) {
		this.connectorContext = connectorContext;
	}
	
	private void sendResponse(ProtocolBuffer.IdentifyKeyRes.ResponseCode responseCode, String username) {
		ProtocolBuffer.IdentifyKeyRes.Builder responseBuilder =
			ProtocolBuffer.IdentifyKeyRes.newBuilder()
				.setCode(responseCode);
		if (username != null) {
			responseBuilder.setUsername(username);
		}
		ProtocolBuffer.IdentifyKeyRes response = responseBuilder.build();
		FractusMessage message = FractusMessage.build(response);
		FractusConnector connector = connectorContext.getFractusConnector();
		connector.sendMessage(message);
	}
	
	private void sendResponse(ProtocolBuffer.IdentifyKeyRes.ResponseCode responseCode) {
		sendResponse(responseCode, null);
	}
	
	@Override
	public void dispatch(byte[] contents) {
		log.debug("Received message to dispatch");
		
		// Deserialize packet contents
		ProtocolBuffer.IdentifyKeyReq request;
		try {
			request = ProtocolBuffer.IdentifyKeyReq.parseFrom(contents);
		} catch (InvalidProtocolBufferException e) {
			log.warn("Received unparsable message", e);
			connectorContext.getFractusConnector().disconnect();
			return;
		}

		String pubKeyEncoding = request.getEncoding();
		ByteString pubKeyByteString = request.getPublicKey();
		log.debug("Received key to identify with encoding: " + pubKeyEncoding);
		log.debug("Key material: " + BinaryUtil.encodeData(pubKeyByteString.toByteArray()));

		// Convert public key data to ECPoint
		X509EncodedKeySpec ks = new X509EncodedKeySpec(pubKeyByteString.toByteArray());
		KeyFactory kf;
		try {
			 kf = java.security.KeyFactory.getInstance("ECDH");
		} catch (NoSuchAlgorithmException e) {
			log.error("Cryptography error: could not initialize ECDH keyfactory!", e);
			return;
		}
		
		ECPublicKey remotePublicKey;
		
		try {
			remotePublicKey = (ECPublicKey)kf.generatePublic(ks);
		} catch (InvalidKeySpecException e) {
			log.warn("Received invalid key specification from client",e);
			return;
		} catch (ClassCastException e) {
			log.warn("Received valid X.509 key from client but it was not EC Public Key material",e);
			return;
		}
		
		ECPoint remotePoint = remotePublicKey.getQ();
		log.debug("Computed target Q point from given ECPK");
		
		// Attempt to identify key
		String keyOwner = null;
		try {
			keyOwner = userTracker.identifyKey(remotePoint, connectorContext.getUsername());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			log.warn("[dispatch]",e1);
		}
		if (keyOwner == null) {
			log.info("Tried to identify unregistered Q: " + BinaryUtil.encodeData(remotePoint.getEncoded()));
			sendResponse(ResponseCode.UNKNOWN_KEY);
			return;
		}

		log.info("Identified key owner as" + keyOwner);
		
		// Verify that remote user should know about this key
		boolean authorized = false;
		try {
			authorized = userTracker.confirmContact(connectorContext.getUsername(), keyOwner);
		} catch (SQLException e) {
			log.error("Encountered database error while confirming contacts", e);
			// TODO: Send error to client
			return;
		}
		
		if (!authorized) {
			log.warn("Unauthorized attempt to identify key owner by ["
					+connectorContext.getUsername()+"] - owner is [" + keyOwner + "]");
			sendResponse(ResponseCode.UNKNOWN_KEY);
			return;
		}
		
		// Owner identified, client authorized.  Send answer.
		log.info("Key requested by " + connectorContext.getUsername() +
				" identified as " + keyOwner + " (Success)");
		sendResponse(ResponseCode.SUCCESS, keyOwner);
	}

}
