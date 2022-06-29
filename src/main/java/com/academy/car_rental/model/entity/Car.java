package com.academy.car_rental.model.entity;

import com.academy.car_rental.model.entity.type.CarType;
import com.academy.car_rental.model.entity.type.EngineType;
import com.academy.car_rental.model.entity.type.GearboxType;
import com.academy.car_rental.validator.ManufacturedYearConstraint;
import com.academy.car_rental.validator.PhoneNumberConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    @NotBlank(message = "Brand is required field")
    private String brand;
    @Column
    @NotBlank(message = "Model is required field")
    private String model;

    @Column(name = "gearbox")
    @Enumerated(EnumType.STRING)
    private GearboxType gearbox;

    @Column(name = "manufactured_year")
    @NotBlank(message = "Manufactured year is required field")
    @ManufacturedYearConstraint
    private String manufacturedYear;

    @Column(name = "engine_type")
    @Enumerated(EnumType.STRING)
    private EngineType engineType;

    @Column(name = "price_per_day")
    private BigDecimal pricePerDay;

    @Column(name = "car_type")
    @Enumerated(EnumType.STRING)
    private CarType carType;

    @Column(name = "image_path")
    private String img;

    @Transient
    public String getImagePath() {
        if (img == null || id == null) return null;

        return "/cars-images/" + id + "/" + img;
    }
}
