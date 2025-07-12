package xdrop.fuzzywuzzy.algorithms;

import xdrop.fuzzywuzzy.StringProcessor;

/**
 * @deprecated Use {@code ToStringFunction#NO_PROCESS} instead.
 */
@Deprecated
public class NoProcess extends StringProcessor {

    @Override
    @Deprecated
    public String process(String in) {
        return in;
    }

}
