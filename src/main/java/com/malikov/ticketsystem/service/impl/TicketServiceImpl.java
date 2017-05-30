package com.malikov.ticketsystem.service.impl;

import com.malikov.ticketsystem.model.Flight;
import com.malikov.ticketsystem.model.Ticket;
import com.malikov.ticketsystem.model.TicketStatus;
import com.malikov.ticketsystem.model.User;
import com.malikov.ticketsystem.repository.ITicketRepository;
import com.malikov.ticketsystem.service.ITicketService;
import com.malikov.ticketsystem.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.malikov.ticketsystem.util.ValidationUtil.checkNotFoundWithId;

/**
 * @author Yurii Malikov
 */
@Service("ticketService")
public class TicketServiceImpl implements ITicketService {

    @Autowired
    private ITicketRepository ticketRepository;

    //@Autowired
    private TaskScheduler scheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());

    // TODO: 5/30/2017 Get rid of it in service!
    private static Map<Long, ScheduledFuture> ticketTaskMap = new HashMap<>();


    // TODO: 5/30/2017 remove annotation?
    @Async
    public ScheduledFuture getDeleteInNotPaidTask(long ticketId) {
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);

        return scheduler.schedule(() -> ticketRepository.deleteIfNotPaid(ticketId),
                new Date(System.currentTimeMillis() + DateTimeUtil.ONE_MINUTE_IN_MILLIS / 3));
    }

    // TODO: 5/30/2017 make it transactional??
    @Override
    public Ticket createNewBookedTicket(Flight flight, User user, BigDecimal price) {

        Integer ticketQuantity = ticketRepository.countTickets(flight.getId());

        if (ticketQuantity >= flight.getAircraft().getModel().getPassengersSeatsQuantity()) {
            // TODO: 5/30/2017 Implement this exception case
        }

        Ticket ticket = save(new Ticket(null, flight, user, null, null, price, null,
                null, null, ticketQuantity + 1, TicketStatus.BOOKED), user.getId());
        if (ticket != null){
            ticketTaskMap.put(ticket.getId(), getDeleteInNotPaidTask(ticket.getId()));
        }
        return ticket;
    }

    @Override
    public Ticket save(Ticket ticket, long userId) {
        Assert.notNull(ticket, "ticket should not be null");
        return ticketRepository.save(ticket, userId);
    }

    @Override
    public Ticket update(Ticket ticket, long userId) {
        // TODO: 5/5/2017 get rid of message  duplicating
        Assert.notNull(ticket, "ticket should not be null");
        return checkNotFoundWithId(ticketRepository.save(ticket, userId), ticket.getId());
    }

    @Override
    public Ticket get(long id, long userId) {
        return checkNotFoundWithId(ticketRepository.get(id, userId), id);
    }

    @Override
    public Ticket getWithUser(long id, long userId) {
        return checkNotFoundWithId(ticketRepository.get(id, userId, Ticket.WITH_USER), id);
    }

    @Override
    public Ticket getWithFlight(long id, long userId) {
        return checkNotFoundWithId(ticketRepository.get(id, userId, Ticket.WITH_FLIGHT), id);
    }

    @Override
    public Ticket getWithUserAndFlight(long id, long userId) {
        return checkNotFoundWithId(ticketRepository.get(id, userId, Ticket.WITH_USER_AND_FLIGHT), id);
    }

    @Override
    public void delete(long id, long userId) {
        checkNotFoundWithId(ticketRepository.delete(id, userId), id);
    }

    @Override
    public List<Ticket> getAll(long userId) {
        return ticketRepository.getAll(userId);
    }

}
