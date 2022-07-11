import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * Each table is a part of a database. This indicates that a Database will have a map of tables.
 * A Database should support operations like creating a table and deleting a table.
 */
public class Database {
    private String name;
    private HashMap <String,Table> tableHashMap;
    private Date createdAt;

    public Database(String name) {
        this.name = name;
        this.tableHashMap = new HashMap<>();
        this.createdAt = new Date();
    }

    public Table createTable (String tableName) {
        if (tableHashMap.containsKey(tableName)) {
            System.err.println("A table already exists with the given name");
        } else {
            Table table = new Table(tableName);
            tableHashMap.put(tableName, table);
            System.out.println("Table successfully created");
        }
        return tableHashMap.get(tableName);
    }

    public void dropTable(String tableName) {
        if(tableHashMap.remove(tableName) == null) {
            System.err.println("Table doesn't exist !");
        } else {
            System.out.println("Table successfully dropped");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Table> getTableHashMap() {
        return tableHashMap;
    }

    public void setTableHashMap(HashMap<String, Table> tableHashMap) {
        this.tableHashMap = tableHashMap;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

