package com.devtale.ratelimitdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static java.lang.Thread.sleep;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *  Integration tests for the Rate Limiting Engine.
 *
 *  These test verify:
 *  - Correct enforcement behaviour
 *  - Proper escalation
 *  - Cooldown forgiveness
 *  - Fairness of TEMP_BLOCK
 *  - Correct HTTP status codes
 *
 *  IMPORTANT:
 *  - Each test runs in a fresh Spring context
 *  - so in memory stores do not leak state.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /*
     * 1. Basic ALLOW Test
     *
     * Purpose:
     * - Verify that normal traffic is NOT blocked.
     * - this ensures the system does not break happy paths.
     */
    @Test
    void firstFewLoginRequestShouldBeAllowed() throws Exception{
        for(int i = 0 ; i<= 5; i ++ ){
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void loginRequestsWithinLimitShouldBeAllowed() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }


    /* ------------------------------------------------------------------
     * 2. TEMP_BLOCK TRIGGER TEST
     * ------------------------------------------------------------------
     * Purpose:
     * Verify that exceeding quota result in TEMP_BLOCK (429).
     */
    @Test
    void exceedingLoginQuotaShouldTriggerTempBlock() throws Exception {

        // Exceed login quota
        for(int i = 0 ; i<=5; i++){
            mockMvc.perform(get("/login")).andExpect(status().isOk());
        }

        // First Violation -> TEMP_BLOCK
        mockMvc.perform(get("/login")).andExpect(status().isTooManyRequests());
    }

    /* ---------------------------------------------------------------------
     * 3. TEMP_BLOCK SHOULD NOT RESET TIMER
     * ---------------------------------------------------------------------
     * Purpose:
     * Verify that retrying during TEMP_BLOCK:
     * - Does NOT reset timer
     * - Does NOT escalate immediately
     */

    @Test
    void hardBlockShouldNotResetTimerOnRety() throws Exception {

        for(int i = 0 ; i <= 5; i++){
            mockMvc.perform(get("/login")).andExpect(status().isOk());
        }

        for(int i = 0; i <3 ; i++){ // TEMP_BLOCK
            mockMvc.perform(get("/login")).andExpect(status().isTooManyRequests());
        }

        for(int i = 0; i< 5; i++){ // HARD_BLOCK
            mockMvc.perform(get("/login")).andExpect(status().isForbidden());
        }
    }

    /* -------------------------------------------------------------------
     *  6. FORGIVENESS AFTER TEMP_BLOCK
     * --------------------------------------------------------------------
     * Purpose:
     * Verify that after cooldown:
     * - User is allowed again
     * - No escalation leakage
     */
    @Test
    void userShouldBeForgivenAfterTempBlockCooldown() throws Exception {

        for(int i = 0; i<=5; i++){
            mockMvc.perform(get("/login")).andExpect(status().isOk());
        }

        mockMvc.perform(get("/login")).andExpect(status().isTooManyRequests());

        // Wait for TEMP_BLOCK  to expire
        Thread.sleep(61_000);

        // Allowed again
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }

    /* ------------------------------------------------------------------
     * 7. FORGIVENESS AFTER HARD_BLOCK
     * -------------------------------------------------------------------
     * Purpose:
     * Verify HARD_BLOCK is time-bounded.
     */
    @Test
    void userShouldBeForgivenAfterHardBlockCooldown() throws Exception{

        for(int i = 0; i<=5; i++){
            mockMvc.perform(get("/login")).andExpect(status().isOk());
        }

        for(int i = 0; i< 3; i++){
            mockMvc.perform(get("/login")).andExpect(status().isTooManyRequests());
        }

        mockMvc.perform(get("/login")).andExpect(status().isForbidden());

        // Wait for HARD_BLOCK to expire (shortened in test config)
        Thread.sleep(610_000);

        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }


    /* --------------------------------------------------------------------
     * 8. SEARCH SHOULD BE MORE LENIENT
     * --------------------------------------------------------------------
     * Purpose:
     * Verify endpoint-specific rules apply.
     */
    @Test
    void searchEndpointShouldAllowMoreRequests() throws Exception {

        for(int i = 0; i<=30; i++){
            mockMvc.perform(get("/search")).andExpect(status().isOk());
        }

        // Eventually throttled
        mockMvc.perform(get("/search")).andExpect(status().isTooManyRequests());
    }

    /* -------------------------------------------------------------------
     * 9. ISOLATION BETWEEN ENDPOINTS
     *
     * Purpose:
     * Abuse on one endpoint must NOT affect others.
     */

    @Test
    void abuseOnLoginShouldNotAffectSearch() throws Exception {

        for(int i = 0; i< 6; i++){
            mockMvc.perform(get("/login")).andExpect(status().isOk());
        }

        // Login blocked
        mockMvc.perform(get("/login")).andExpect(status().isTooManyRequests());

        // Search still allowed
        mockMvc.perform(get("/search")).andExpect(status().isOk());
    }





}
