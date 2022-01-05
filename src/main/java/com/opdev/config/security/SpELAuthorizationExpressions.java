package com.opdev.config.security;

public class SpELAuthorizationExpressions {

    /**
     * Verifies that the caller is authenticated as:
     * <ol>
     *     <li>
     *      {@link Roles#ADMIN}
     *     </li>
     *     <li>
     *         {@link Roles#COMPANY}
     *         <ul>
     *             <li>
     *                 In this case, SpEL checks for a {@link org.springframework.web.bind.annotation.PathVariable}
     *                 {@code username} and matches it against {@code authentication.name}
     *             </li>
     *         </ul>
     *     </li>
     * </ol>
     */
    public static final String asMatchingCompanyOrAdmin =
            "(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))"
                    + " || hasRole('" + Roles.ADMIN + "')";

    /**
     * Verifies that the caller is authenticated as:
     * <ul>
     *     <li>
     *         {@link Roles#TALENT}
     *         <ul>
     *             <li>
     *                 SpEL checks for a {@link org.springframework.web.bind.annotation.PathVariable}
     *                 {@code username} and matches it against {@code authentication.name}
     *             </li>
     *         </ul>
     *     </li>
     * </ul>
     */
    public static final String asMatchingTalent =
            "#username == authentication.name && hasRole('" + Roles.TALENT + "')";

    public static final String asMatchingTalentOrCompany =
            "(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))"
                    + " || hasRole('" + Roles.TALENT + "')";

    public static final String isTalent =
            "hasRole('" + Roles.TALENT + "')";

    public static final String isTalentOrCompany =
            "hasAnyRole('" + Roles.TALENT + "', '" + Roles.COMPANY + "')";

    public static final String isAuthenticated =
            "isAuthenticated()";

}
