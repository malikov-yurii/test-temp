package com.malikov.ticketsystem.service.impl;

import com.malikov.ticketsystem.model.Airport;
import com.malikov.ticketsystem.service.AbstractServiceTest;
import com.malikov.ticketsystem.service.IAirportService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.malikov.ticketsystem.AirportTestData.*;
import static com.malikov.ticketsystem.CityTestData.ROME;

/**
 * @author Yurii Malikov
 */
public class AirportServiceImplTest extends AbstractServiceTest {

    @Autowired
    protected IAirportService service;

    @Test
    public void save() throws Exception {
        Airport newAirport = getNewDummyAirportWithNullId(null);
        Airport created = service.save(newAirport);
        newAirport.setId(created.getId());
        MATCHER.assertCollectionEquals(
                getTestDataAirportsWith(newAirport),
                service.getAll());
    }

    @Test
    public void update() throws Exception {
        Airport updated = new Airport(AIRPORT_3_LUTON);
        updated.setName("NewAirportName");
        service.update(updated);
        MATCHER.assertEquals(updated, service.get(updated.getId()));
    }

    @Test
    public void get() throws Exception {
        Airport airport = service.get(AIRPORT_1_BORISPOL.getId());
        MATCHER.assertEquals(AIRPORT_1_BORISPOL, airport);
    }

    @Test
    public void getAll() throws Exception {
        MATCHER.assertCollectionEquals(AIRPORTS, service.getAll());
    }

    // TODO: 5/6/2017 How dto test delete despite constraints? At first I sould delete planes? perhaps
    @Test(expected = PersistenceException.class)
    public void delete() throws Exception {
        service.delete(AIRPORT_4_DA_VINCI.getId());
        MATCHER.assertCollectionEquals(Arrays.asList(AIRPORT_1_BORISPOL, AIRPORT_2_HEATHROW, AIRPORT_3_LUTON), service.getAll());
    }

    @Test
    public void getByNameMask() throws Exception {
        List<Airport> airportsByMask = service.getByNameMask("Bor");
        MATCHER.assertCollectionEquals(Collections.singleton(AIRPORT_1_BORISPOL), airportsByMask);
    }

    @Test
    public void getByName() throws Exception {
        Airport airportByName = service.getByName("Heathrow Airport");
        MATCHER.assertEquals(AIRPORT_2_HEATHROW, airportByName);
    }

    private Airport getNewDummyAirportWithNullId(Long id) {
        return new Airport(id, "newAirportName", ROME);
    }

    private List<Airport> getTestDataAirportsWith(Airport newAirport) {
        List<Airport> airportssWithNewUser = new ArrayList<>(AIRPORTS);
        airportssWithNewUser.add(newAirport);
        return airportssWithNewUser;
    }

}
