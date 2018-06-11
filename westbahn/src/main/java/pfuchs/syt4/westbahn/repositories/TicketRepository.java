package pfuchs.syt4.westbahn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfuchs.syt4.westbahn.model.Reservierung;
import pfuchs.syt4.westbahn.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
