package app.service.component;

import app.entities.Component;
import app.exceptions.DatabaseException;
import app.mapper.component.ComponentMapper;
import app.persistence.ConnectionPool;

import java.util.List;

public class ComponentService {
    public static void saveOrderComponentsToDB (List<Component> orderComponents, ConnectionPool connectionPool) throws DatabaseException {
        ComponentMapper.saveOrderComponentsToDB(orderComponents, connectionPool);
    }
}
