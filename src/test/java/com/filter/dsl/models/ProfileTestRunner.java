package com.filter.dsl.models;

public class ProfileTestRunner {
    public static void main(String[] args) {
        ProfileTest test = new ProfileTest();
        
        try {
            System.out.println("Running Profile tests...\n");
            
            test.testProfileBuilderWithPermanentAttributes();
            System.out.println("✓ testProfileBuilderWithPermanentAttributes");
            
            test.testFirstReferral();
            System.out.println("✓ testFirstReferral");
            
            test.testCustomProperties();
            System.out.println("✓ testCustomProperties");
            
            test.testSetCustomPropertyAfterCreation();
            System.out.println("✓ testSetCustomPropertyAfterCreation");
            
            test.testComputedPropertyExpressions();
            System.out.println("✓ testComputedPropertyExpressions");
            
            test.testDefineComputedPropertyAfterCreation();
            System.out.println("✓ testDefineComputedPropertyAfterCreation");
            
            test.testSetFirstReferralFromVisit();
            System.out.println("✓ testSetFirstReferralFromVisit");
            
            test.testSetFirstReferralFromNonFirstVisit();
            System.out.println("✓ testSetFirstReferralFromNonFirstVisit");
            
            test.testStandardComputedPropertiesTemplate();
            System.out.println("✓ testStandardComputedPropertiesTemplate");
            
            test.testCompleteProfile();
            System.out.println("✓ testCompleteProfile");
            
            test.testEmptyProfile();
            System.out.println("✓ testEmptyProfile");
            
            test.testFirstReferralDirectSource();
            System.out.println("✓ testFirstReferralDirectSource");
            
            System.out.println("\n✅ All tests passed!");
            
        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
