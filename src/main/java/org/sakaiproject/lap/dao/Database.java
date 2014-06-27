/**
 * Copyright 2008 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.sakaiproject.lap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.db.api.SqlService;

/**
 * General database operations
 * Uses the SqlService class from Sakai
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 */
public class Database {

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Borrows a connection from the pool
     * 
     * @return the connection object
     */
    private Connection borrowConnection() {
        Connection connection = null;

        try {
            connection = sqlService.borrowConnection();
        } catch (Exception e) {
            log.error("Cannot get database connection: " + e, e);
        }

        return connection;
    }

    /**
     * Returns the borrowed connection to the pool
     * 
     * @param connection the connection object
     */
    private void returnConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                sqlService.returnConnection(connection);
            }
        } catch (Exception e) {
            log.error("Error returning connection to pool: " + e, e);
        }
    }

    /**
     * Prepares the prepared statement with the SQL provided
     * 
     * @param preparedStatement the prepared statement
     * @param sql the SQL string
     * @return the prepared statement
     */
    protected PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String sql) {
        Connection connection = borrowConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (Exception e) {
            log.error("Error creating prepared statement: " + e, e);
        }

        return preparedStatement;
    }

    /**
     * Closes a prepared statement, after returning the borrowed connection
     * 
     * @param preparedStatement the prepared statement
     */
    protected void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                try {
                    returnConnection(preparedStatement.getConnection());
                } catch (Exception e) {
                    log.error("Error returning connection to pool: " + e, e);
                } finally {
                    preparedStatement.close();
                }
            }
        } catch (Exception e) {
            log.error("Error closing prepared statement: " + e, e);
        }
    }

    /**
     * Executes the given prepared statement
     * 
     * @param preparedStatement the prepared statement
     * @return the ResultSet from the query
     */
    protected ResultSet executePreparedStatement(PreparedStatement preparedStatement) {
        ResultSet resultSet = null;

        try {
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            log.error("Cannot perform database call: " + e, e);
        }

        return resultSet;
    }

    private SqlService sqlService;
    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

}
