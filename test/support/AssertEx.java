package support;

import org.junit.Assert;

public final class AssertEx {

    private AssertEx() {
    }

    public static <T extends Throwable> T assertThrows(
            Class<T> expected,
            Runnable runnable
    ) {
        try {
            runnable.run();
        } catch (Throwable thrown) {
            if (expected.isInstance(thrown)) {
                return expected.cast(thrown);
            }
            Assert.fail("Expected " + expected.getName() + " but got " + thrown.getClass().getName() + ": " + thrown.getMessage());
        }

        Assert.fail("Expected " + expected.getName() + " but nothing was thrown.");
        return null; // unreachable
    }

    public static void assertDoesNotThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable thrown) {
            Assert.fail("Expected no exception, but got " + thrown.getClass().getName() + ": " + thrown.getMessage());
        }
    }
}

