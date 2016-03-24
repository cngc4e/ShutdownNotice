package net.pl3x.bukkit.shutdownnotice;

public class ServerStatus {
    public enum State {
        RUNNING, // server is running
        SHUTDOWN, // server is shutting down
        RESTART // server is restarting
    }

    private static ServerStatus status;

    public static ServerStatus getStatus() {
        if (status == null) {
            status = new ServerStatus();
            status.setStatus(State.RUNNING, null, null);
        }
        return status;
    }

    private State state;
    private Integer timeLeft;
    private String reason;

    public void setStatus(State state, Integer timeLeft, String reason) {
        this.state = state;
        this.timeLeft = timeLeft;
        this.reason = reason;
    }

    public State getState() {
        return state;
    }

    public Integer getTimeLeft() {
        return timeLeft;
    }

    public String getReason() {
        return reason;
    }
}
