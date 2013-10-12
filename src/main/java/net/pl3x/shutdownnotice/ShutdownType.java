package net.pl3x.shutdownnotice;

public enum ShutdownType {
	SHUTDOWN("shutdown"),
	RESTART("restart"),
	REBOOT("reboot");
	
	private String name;
	
	private ShutdownType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
