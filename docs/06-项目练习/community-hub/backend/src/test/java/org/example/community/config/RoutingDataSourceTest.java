package org.example.community.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoutingDataSourceTest {

    @AfterEach
    void clear() {
        DataSourceContext.clear();
    }

    @Test
    void defaultsToMaster() {
        RoutingDataSource dataSource = new RoutingDataSource();
        assertEquals(DataSourceType.MASTER, dataSource.determineCurrentLookupKey());
    }

    @Test
    void slaveUntilCleared() {
        RoutingDataSource dataSource = new RoutingDataSource();
        DataSourceContext.useSlave();
        assertEquals(DataSourceType.SLAVE, dataSource.determineCurrentLookupKey());
        DataSourceContext.clear();
        assertEquals(DataSourceType.MASTER, dataSource.determineCurrentLookupKey());
    }
}
