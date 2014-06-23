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

public class Db {

    private final Log log = LogFactory.getLog(Db.class);

    private Connection borrowConnection() {
        Connection connection = null;

        try {
            connection = sqlService.borrowConnection();
        } catch (Exception e) {
            log.error("Cannot get database connection: " + e, e);
        }

        return connection;
    }

    private void returnConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                sqlService.returnConnection(connection);
            }
        } catch (Exception e) {
            log.error("Error returning connection to pool: " + e, e);
        }
    }

    protected PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String sql) {
        Connection connection = borrowConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (Exception e) {
            log.error("Error creating prepared statement: " + e, e);
        }

        return preparedStatement;
    }

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
