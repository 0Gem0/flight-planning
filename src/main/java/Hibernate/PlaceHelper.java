package Hibernate;

import Hibernate.entity.Place;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class PlaceHelper {
    private Session session;

    public PlaceHelper(Session session) {
        this.session = session;
    }
    public List<Place> getPlaceList(){
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Place> criteriaQuery = criteriaBuilder.createQuery(Place.class);
        Root<Place> root = criteriaQuery.from(Place.class);
        Query query = session.createQuery(criteriaQuery);
        return (List<Place>) query.getResultList();
    }
}
