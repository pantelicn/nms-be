package com.opdev.model.subscription;


import com.opdev.model.Audit;
import com.opdev.model.user.User;
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
import java.time.Instant;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
@Entity
@Table(name = "plan_product")
public class PlanProduct extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = true)
    private Integer quantity;

    @NonNull
    @Column(nullable = false)
    private Boolean limited;

    @ManyToOne
    @JoinColumn(name = "plan_id", referencedColumnName = "id", nullable = false)
    @Setter
    private Plan plan;

    @OneToOne
    @JoinColumn(name = "product", referencedColumnName = "id", nullable = false)
    @Setter
    private Product product;

    public void update(PlanProduct modified, User modifiedBy) {
        quantity = modified.quantity;
        limited = modified.limited;
        setModifiedBy(modifiedBy);
        setModifiedOn(Instant.now());
    }

    public boolean planIdEquals(Long planId) {
        return plan.getId().equals(planId);
    }

    public boolean productIdEquals(Long productId) {
        return product.getId().equals(productId);
    }

}
