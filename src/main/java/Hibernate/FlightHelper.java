package Hibernate;

import Hibernate.entity.Flight;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class FlightHelper {
    private Session session;

    public FlightHelper(Session session) {
        this.session = session;
    }
    public List<Flight> getFlightList(){
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Flight> criteriaQuery = criteriaBuilder.createQuery(Flight.class);
        Root<Flight> root = criteriaQuery.from(Flight.class);
        Query query = session.createQuery(criteriaQuery);
        List<Flight> flights = query.getResultList();
        return flights;
    }
}
