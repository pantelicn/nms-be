package com.opdev.model.subscription;

import com.opdev.model.Audit;
import com.opdev.model.user.User;
import com.opdev.util.converters.MoneyAttributeConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.SerializationUtils;
import org.joda.money.Money;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
@Entity
@Table(name = "plan")
public class Plan extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(name = "duration_in_months", nullable = false)
    private Integer durationInMonths;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType type;

    @NonNull
    @Column(nullable = false)
    private String description;

    @NonNull
    @Column(nullable = false)
    @Convert(converter = MoneyAttributeConverter.class)
    private Money price;

    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<PlanProduct> products = new ArrayList<>();

    public void update(Plan modified, User loggedUser) {
        name = modified.name;
        type = modified.type;
        price = SerializationUtils.clone(modified.price);
        setModifiedOn(Instant.now());
        setModifiedBy(loggedUser);
    }
}
