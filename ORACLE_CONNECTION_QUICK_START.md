# Oracle Database Connection - Quick Start Guide

## 🚀 Quick Setup Steps

### 1. **Database Setup** (Run as SYSDBA)

```sql
-- Connect to Oracle as SYSDBA
sqlplus / as sysdba

-- Run the complete setup script
@sql/setup_oracle_database.sql
```

### 2. **Create Tables** (Run as crave_kitchen user)

```sql
-- Connect as crave_kitchen user
sqlplus crave_kitchen/crave_kitchen123@localhost:1521:XE

-- Create menu tables
@sql/oracle_menu_tables.sql

-- Create triggers
@sql/oracle_triggers.sql
```

### 3. **Application Configuration**

The `application.properties` is already configured for Oracle:

```properties
# Oracle Database Configuration
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.driverClassName=oracle.jdbc.OracleDriver
spring.datasource.username=crave_kitchen
spring.datasource.password=crave_kitchen123
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=validate
```

### 4. **Test Connection**

```bash
# Test database connection
sqlplus crave_kitchen/crave_kitchen123@localhost:1521:XE

# Start the application
mvn spring-boot:run
```

## 🔧 Connection String Variations

### Oracle XE (Express Edition)

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
```

### Oracle Standard/Enterprise

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:ORCL
```

### Oracle Cloud/Remote

```properties
spring.datasource.url=jdbc:oracle:thin:@your-host:1521:your-service-name
```

## 🛠️ Troubleshooting

### Connection Issues

1. **Verify Oracle is running:**

   ```bash
   # Windows
   services.msc  # Check OracleServiceXE

   # Linux
   systemctl status oracle-xe
   ```

2. **Test connectivity:**

   ```bash
   telnet localhost 1521
   ```

3. **Check service name:**
   ```sql
   SELECT name, value FROM v$parameter WHERE name = 'service_names';
   ```

### Authentication Issues

1. **Verify user exists:**

   ```sql
   SELECT username, account_status FROM dba_users WHERE username = 'CRAVE_KITCHEN';
   ```

2. **Check privileges:**
   ```sql
   SELECT privilege FROM dba_sys_privs WHERE grantee = 'CRAVE_KITCHEN';
   ```

### Driver Issues

1. **Verify JDBC driver in pom.xml:**
   ```xml
   <dependency>
       <groupId>com.oracle.database.jdbc</groupId>
       <artifactId>ojdbc8</artifactId>
       <version>21.9.0.0</version>
   </dependency>
   ```

## 📁 SQL Files Overview

| File                        | Purpose                  | Run As        |
| --------------------------- | ------------------------ | ------------- |
| `setup_oracle_database.sql` | Complete database setup  | SYSDBA        |
| `oracle_menu_tables.sql`    | Menu-related tables      | crave_kitchen |
| `oracle_sequences.sql`      | ID generation sequences  | SYSDBA        |
| `oracle_triggers.sql`       | Automatic timestamps/IDs | crave_kitchen |

## 🔒 Security Notes

1. **Change default password** `crave_kitchen123` in production
2. **Use environment variables** for sensitive data
3. **Enable SSL/TLS** for remote connections
4. **Configure firewall rules** appropriately

## 📊 Monitoring

### Connection Pool Status

```sql
SELECT * FROM v$session WHERE username = 'CRAVE_KITCHEN';
```

### Database Performance

```sql
SELECT * FROM v$sql WHERE parsing_schema_name = 'CRAVE_KITCHEN';
```

## 🎯 Next Steps

1. ✅ Run database setup scripts
2. ✅ Verify connection
3. ✅ Start application
4. ✅ Test API endpoints
5. ✅ Monitor logs for any issues

## 📞 Support

If you encounter issues:

1. Check Oracle logs: `$ORACLE_HOME/diag/rdbms/.../trace/`
2. Verify application logs for connection errors
3. Test with SQL\*Plus first
4. Check firewall and network connectivity
