package web.model;

public class Car {
    private String model;
    private String number;
    private int release;

    public Car() {
    }

    public Car(String model, String number, int release) {
        this.model = model;
        this.number = number;
        this.release = release;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    @Override
    public String toString() {
        return "Car{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", release=" + release +
                '}';
    }
}
