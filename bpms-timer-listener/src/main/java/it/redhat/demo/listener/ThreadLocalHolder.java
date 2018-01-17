package it.redhat.demo.listener;

public class ThreadLocalHolder {

	public static final ThreadLocal<String> requestThreadLocal = new ThreadLocal<String>();

	public static void set(String actionContext) {
        requestThreadLocal.set(actionContext);
	}

	public static void unset() {
		requestThreadLocal.remove();
	}

	public static String get() {
		return requestThreadLocal.get();
	}
}