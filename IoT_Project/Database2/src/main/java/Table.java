import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * A table will consist of several rows. It should support all the CRUD operations.
 * It supports functionalities like adding a row, updating a row, deleting a row, and reading a row.
 */
public class Table {
    private String tableName;
    private HashMap <String,Row> rows;
    private Date createdAt;

    private static final int LINE = 20;
    private static final String SPACE = "%-"+LINE+"s";
    private Vector<String> columnsNames;

    public Table (String tableName) {
        this.tableName = tableName;
        this.rows = new HashMap<>();
        this.createdAt = new Date();
        this.columnsNames = new Vector<>();
        this.columnsNames.add("Row ID");
    }

    public void show () {
        System.out.printf("-".repeat(LINE * columnsNames.size()) + "\n");

        // Show Columns
        for(String columnName : columnsNames){
            System.out.printf(SPACE, columnName);
        }
        System.out.printf("\n" + "=".repeat(LINE * columnsNames.size()) + "\n");

        // Show Rows
        for (String rowName : rows.keySet()) {
            for (String columnName : columnsNames){
                String value = rows.get(rowName).getValue(columnName);
                System.out.printf(SPACE, value);
            }
            System.out.println();
        }

        System.out.printf("-".repeat(LINE * columnsNames.size()) + "\n");
    }


    // CRUD Methods

    public boolean entryExists(String rowId){
        if (rows.containsKey(rowId)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean createEntry(String rowId, HashMap <String,String> columnsMap) {
        /*if (rows.containsKey(rowId)) {
            System.err.println("Duplicate primary key : " + " Insertion failed");
            return false;
        } else {*/
        Row row =  new Row(rowId, columnsMap, new Date(), new Date());
        rows.put(rowId, row);
        System.out.println("Successfully added a row");
        checkColumns(columnsMap);
        return true;
        //}
    }

    public HashMap <String,String> readEntry (String rowId) {
        return rows.get(rowId).getColumnValueMap();
    }

    public void updateEntry (String rowId, HashMap <String,String> newColumnsMap) {
        Row row = rows.get(rowId);
        newColumnsMap.forEach( (k,v)->{row.getColumnValueMap().put(k,v);} );
        row.setUpdatedAt(new Date());
        checkColumns(newColumnsMap);
        System.out.println("Row successfully updated");
    }

    public void deleteEntry (String rowId) {
        rows.remove(rowId);
        System.out.println("Row successfully deleted");
    }

    private void checkColumns (HashMap<String,String> columnMap) {
        for(String columnName : columnMap.keySet()) {
            if(!columnsNames.contains(columnName)){
                columnsNames.add(columnName);
            }
        }
    }

    // Getters & Setters

    public HashMap<String, Row> getRows() {
        return rows;
    }

    public void setRows(HashMap<String, Row> rows) {
        this.rows = rows;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
