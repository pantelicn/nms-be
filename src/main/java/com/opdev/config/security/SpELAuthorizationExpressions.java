package com.opdev.config.security;

import org.springframework.security.access.prepost.PreAuthorize;

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
    public static final String AS_MATCHING_COMPANY_OR_ADMIN =
            "(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))"
                    + " || hasRole('" + Roles.ADMIN + "')";

    /**
     * Verifies that the caller is authenticated as:
     * <ol>
     *     <li>
     *      {@link Roles#ADMIN}
     *     </li>
     *     <li>
     *         {@link Roles#TALENT}
     *         <ul>
     *             <li>
     *                 In this case, SpEL checks for a {@link org.springframework.web.bind.annotation.PathVariable}
     *                 {@code username} and matches it against {@code authentication.name}
     *             </li>
     *         </ul>
     *     </li>
     * </ol>
     */
    public static final String AS_MATCHING_TALENT_OR_ADMIN =
            "(#username == authentication.name && hasRole('" + Roles.TALENT + "'))"
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
    public static final String AS_MATCHING_TALENT =
            "#username == authentication.name && hasRole('" + Roles.TALENT + "')";

    public static final String AS_MATCHING_TALENT_OR_COMPANY =
            "(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))"
                    + " || hasRole('" + Roles.TALENT + "')";

    public static final String IS_TALENT =
            "hasRole('" + Roles.TALENT + "')";

    public static final String IS_TALENT_OR_COMPANY =
            "hasAnyRole('" + Roles.TALENT + "', '" + Roles.COMPANY + "')";

    public static final String IS_AUTHENTICATED =
            "isAuthenticated()";

    /**
     * Verifies that the caller is authenticated as admin
     */
    public static final String IS_ADMIN = "hasRole('" + Roles.ADMIN + "')";

    public static final String HAS_ANY_ROLE_TALENT_OR_COMPANY = "(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))";

}
