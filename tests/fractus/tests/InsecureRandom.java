package fractus.tests;

import java.util.Random;

public class InsecureRandom
extends Random {
	byte current;
	
	public byte nextByte() {
		current++;
		// Make this fail every second nonce
		current %= 96*2/8;
		return current;
	}
	
	@Override
	protected int next(int bits) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean nextBoolean() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double nextDouble() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float nextFloat() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized double nextGaussian() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int nextInt() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int nextInt(int n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long nextLong() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void setSeed(long seed) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void nextBytes(byte[] bytes) {
		for (int i=0; i < bytes.length; i++) {
			bytes[i] = nextByte();
		}
	}
	private static final long serialVersionUID = -8559864239461472411L;

}
