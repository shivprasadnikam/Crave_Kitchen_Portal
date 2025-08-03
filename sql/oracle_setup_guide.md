# Oracle Database Setup Guide for Crave Kitchen Portal

## Prerequisites

1. **Oracle Database Installation**

   - Oracle Database 19c or 21c (XE, Standard, or Enterprise Edition)
   - Oracle SQL Developer or SQL\*Plus for database administration

2. **Java Requirements**
   - Java 17 or higher
   - Oracle JDBC Driver (already included in pom.xml)

## Database Setup Steps

### 1. Create Database User

Connect to Oracle as SYSDBA and run:

```sql
-- Connect as SYSDBA
-- sqlplus / as sysdba

-- Create tablespace
CREATE TABLESPACE crave_kitchen_data
DATAFILE 'crave_kitchen_data.dbf'
SIZE 100M
AUTOEXTEND ON NEXT 50M MAXSIZE 1G;

-- Create user
CREATE USER crave_kitchen IDENTIFIED BY crave_kitchen123
DEFAULT TABLESPACE crave_kitchen_data
QUOTA UNLIMITED ON crave_kitchen_data;

-- Grant necessary privileges
GRANT CONNECT, RESOURCE TO crave_kitchen;
GRANT CREATE SESSION TO crave_kitchen;
GRANT CREATE TABLE TO crave_kitchen;
GRANT CREATE SEQUENCE TO crave_kitchen;
GRANT CREATE VIEW TO crave_kitchen;
GRANT CREATE PROCEDURE TO crave_kitchen;
GRANT CREATE TRIGGER TO crave_kitchen;
GRANT UNLIMITED TABLESPACE TO crave_kitchen;

-- Grant additional privileges for JPA/Hibernate
GRANT SELECT ANY DICTIONARY TO crave_kitchen;
GRANT SELECT ANY TABLE TO crave_kitchen;
GRANT INSERT ANY TABLE TO crave_kitchen;
GRANT UPDATE ANY TABLE TO crave_kitchen;
GRANT DELETE ANY TABLE TO crave_kitchen;
```

### 2. Connection String Configuration

Update the connection string in `application.properties` based on your Oracle setup:

#### For Oracle XE (Express Edition):

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
```

#### For Oracle Standard/Enterprise Edition:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:ORCL
```

#### For Oracle Cloud/Remote Database:

```properties
spring.datasource.url=jdbc:oracle:thin:@your-host:1521:your-service-name
```

### 3. Database Schema Creation

Run the SQL scripts in the following order:

1. `oracle_menu_tables.sql` - Creates all menu-related tables
2. `oracle_sequences.sql` - Creates sequences for ID generation
3. `oracle_triggers.sql` - Creates triggers for automatic ID and timestamp management

### 4. Application Configuration

#### Development Environment:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

#### Production Environment:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN
```

### 5. Connection Pool Optimization

The application is configured with HikariCP connection pool:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## Troubleshooting

### Common Issues:

1. **Connection Refused**

   - Verify Oracle service is running
   - Check port 1521 is accessible
   - Verify service name (XE, ORCL, etc.)

2. **Authentication Failed**

   - Verify username/password
   - Check user has CONNECT privilege
   - Ensure user is not locked

3. **Tablespace Issues**

   - Verify tablespace exists
   - Check user has quota on tablespace
   - Ensure tablespace has sufficient space

4. **Driver Issues**
   - Verify ojdbc8.jar is in classpath
   - Check driver class name is correct

### Testing Connection:

```bash
# Test with SQL*Plus
sqlplus crave_kitchen/crave_kitchen123@localhost:1521:XE

# Test with application
mvn spring-boot:run
```

## Security Considerations

1. **Change Default Passwords**

   - Update `crave_kitchen123` to a strong password
   - Use environment variables for sensitive data

2. **Network Security**

   - Configure firewall rules
   - Use SSL/TLS for remote connections
   - Implement connection encryption

3. **Database Security**
   - Grant minimum required privileges
   - Regular security patches
   - Audit logging enabled

## Environment Variables

For production, use environment variables:

```properties
spring.datasource.url=${ORACLE_URL:jdbc:oracle:thin:@localhost:1521:XE}
spring.datasource.username=${ORACLE_USERNAME:crave_kitchen}
spring.datasource.password=${ORACLE_PASSWORD:crave_kitchen123}
```

## Monitoring

1. **Connection Pool Monitoring**

   - Monitor active connections
   - Check connection timeouts
   - Review connection pool statistics

2. **Performance Monitoring**

   - Monitor SQL execution times
   - Check for slow queries
   - Review database statistics

3. **Health Checks**
   - Database connectivity
   - Connection pool health
   - Application readiness
