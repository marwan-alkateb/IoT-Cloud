import java.util.Date;
import java.util.HashMap;

public class Row {
    private HashMap <String,String> columnValueMap;
    private Date createdAt;
    private Date updatedAt;

    public Row(String rowId, HashMap<String, String> columnsMap, Date createdAt, Date updatedAt) {
        this.columnValueMap = columnsMap;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        columnValueMap.put("Row ID", rowId);
    }

    public HashMap<String,String> getColumnValueMap() {
        return columnValueMap;
    }

    // Getters & Setters

    public  int getNumberOfColumns () {
        return columnValueMap.size();
    }

    public void setColumnValueMap(HashMap<String,String> columnValueMap) {
        this.columnValueMap = columnValueMap;
    }

    public String getRowId() {
        return columnValueMap.get("Row ID");
    }

    public void setRowId(String rowId) {
        columnValueMap.replace("Row ID",columnValueMap.get("Row ID"),rowId);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getValue (String columnName) {
        return columnValueMap.get(columnName);
    }
}
