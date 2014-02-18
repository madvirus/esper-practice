package esper;

public class Entering {
    private String ticketId;

    public Entering(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketId() {
        return ticketId;
    }

    @Override
    public String toString() {
        return "Entering{" +
                "ticketId='" + ticketId + '\'' +
                '}';
    }
}
