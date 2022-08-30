package com.opdev.model.subscription;

import com.opdev.model.Audit;
import com.opdev.model.company.Company;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString(callSuper = true)
@Builder
@Entity
@Table(name = "product_usage")
public class ProductUsage extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Setter
    private Integer remaining;

    @NonNull
    @Column(nullable = false)
    private Boolean limited;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Period period;

    @ToString.Exclude
    @NonNull
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @NonNull
    @OneToOne
    @JoinColumn(name = "product", referencedColumnName = "id", nullable = false)
    private Product product;

}
