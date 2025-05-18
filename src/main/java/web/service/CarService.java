package web.service;

import org.springframework.stereotype.Service;
import web.model.Car;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CarService {

    private final List<Car> carRepo = Stream
            .iterate(1, i -> i < 6, i -> ++i)
            .map(elem -> new Car("name" + elem, "number" + elem, 2000 + elem))
            .toList();

    public List<Car> getCars(int counter) {
        return carRepo
                .stream()
                .limit(counter)
                .toList();
    }

}
