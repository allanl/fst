import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for FST (Free Subliminal Text).
 * This is a basic test class to verify the testing setup works correctly.
 */
public class FSTTest {

    private FST fst;

    @BeforeEach
    public void setUp() {
        // Initialize FST for testing
        // Note: This is a simplified setup for demonstration purposes
        fst = new FST();
    }

    @Test
    @DisplayName("Test FST initialization")
    public void testFSTInitialization() {
        // Verify FST is initialized correctly
        assertThat(fst).isNotNull();
    }

    @Test
    @DisplayName("Test default font settings")
    public void testDefaultFontSettings() {
        // Verify default font settings
        assertThat(FST.font).isNotNull();
        assertThat(FST.font.getFamily()).isEqualTo("Dialog");
        assertThat(FST.font.getSize()).isEqualTo(35);
    }

    @Test
    @DisplayName("Test default color settings")
    public void testDefaultColorSettings() {
        // Verify default color settings
        assertThat(FST.fontColour).isNotNull();
        assertThat(FST.fontColour.getAlpha()).isEqualTo(30);
        assertThat(FST.fontColour.getBlue()).isEqualTo(255);
    }
}
