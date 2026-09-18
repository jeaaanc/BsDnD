package BankSdNd.example.BsDnD;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "BankSdNd.example.BsDnD", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String CORE_PACKAGE = "..core..";
    private static final String ADAPTER_PACKAGE = "..adapter..";
    private static final String SPRING_PACKAGE = "org.springframework..";
    private static final String DOMAIN_PACKAGE = "..core.domain..";
    private static final String APPLICATION_PACKAGE = "..core.application..";


    @ArchTest
    static final ArchRule coreShouldNotDependOnAdapters = noClasses()
            .that().resideInAPackage(CORE_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(ADAPTER_PACKAGE)
            .because("Core (Business Rules) must not know delivery details (Web/CLI)" +
                    " or persistence mechanisms (adapter).");

    @ArchTest
    static final ArchRule coreShouldNotDependOnSpringFramework = noClasses()
            .that().resideInAPackage(CORE_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(SPRING_PACKAGE)
            .because("Core must be pure Java, ensuring the domain is not coupled to the Spring Framework.");

    @ArchTest
    static final ArchRule domainShouldNotDependOnApplication = noClasses()
            .that().resideInAPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(APPLICATION_PACKAGE)
            .because("Domain (Entities) is the absolute center and must not depend on Use Cases (Application).");

}
