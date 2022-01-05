package com.opdev.model.search;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.opdev.model.Audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
@Entity
@Table
public class Facet extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    /**
     * The name of the table for which the facet was created. It might show up as a
     * search type on the UI.
     */
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name_table", nullable = false)
    private TableName tableName;

    /**
     * It references the `code` field in the table for which the facet was created
     * ({@link #tableName}).
     */
    @NonNull
    @Column(nullable = false)
    private String code;

    @NonNull
    @Column(nullable = false)
    private String value;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operator_type", nullable = false)
    private OperatorType operatorType;

    @ManyToOne
    @JoinColumn(name = "search_template_id", referencedColumnName = "id", nullable = false)
    @Setter
    private SearchTemplate searchTemplate;

}
