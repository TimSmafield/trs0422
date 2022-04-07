import java.util.Objects;

public class Tool {

    private final String toolCode;
    private final ToolType toolType;
    private final Brand brand;
    private boolean inStock = false;

    public Tool(String toolCode, ToolType toolType, Brand brand, boolean inStock) {
        this.toolCode = toolCode;
        this.toolType = toolType;
        this.brand = brand;
        this.inStock = inStock;
    }

    public String getToolCode() {
        return toolCode;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public Brand getBrand() {
        return brand;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tool tool = (Tool) o;
        return toolCode.equals(tool.toolCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toolCode);
    }
}


