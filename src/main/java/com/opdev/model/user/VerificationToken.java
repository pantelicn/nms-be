package com.opdev.model.user;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.opdev.model.Audit;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "verification_token")
public class VerificationToken extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private UUID activationCode;

    @OneToOne(mappedBy = "verificationToken")
    private User user;

    @Builder.Default
    private Boolean used = Boolean.FALSE;

    public void use(){
        this.used = true;
    }

}
