package tud.tangram.svgplot.data.trendline;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicModuloInteger extends AtomicInteger {

	private static final long serialVersionUID = 1L;
	
	private int mod;

	public AtomicModuloInteger(int mod) {
		super();
		this.mod = mod;
	}
	
	public AtomicModuloInteger(int initialValue, int mod) {
		super(initialValue);
		this.mod = mod;
	}
	
	public int getAndIncrementMod() {
		int get = getAndIncrement();
		set(get() % mod);
		return get;
	}
	
	public int incrementAndGetMod() {
		set(get() % mod);
		return get();
	}
	
	public void setMod(int newValue) {
		set(newValue % mod);
	}
	
	public int addAndGetMod(int delta) {
		set(((addAndGet(delta) % mod) + mod) % mod);
		return get();
	}
	
	public int getAndAddMod(int delta) {
		int get = getAndAdd(delta);
		setMod(get());
		return get;
	}
	
	public int getModDelta(int delta) {
		return ((get() + delta) % mod + mod) % mod;
	}
}
