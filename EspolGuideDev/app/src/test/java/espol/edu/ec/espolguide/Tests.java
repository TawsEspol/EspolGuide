package espol.edu.ec.espolguide;


import org.junit.Test;

import org.junit.Assert.*;

import static org.junit.Assert.assertEquals;

import org.mockito.Mock;
import org.mockito.junit.*;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.viewModels.LoginViewModel;


/**
 * Created by fabricio on 12/08/18.
 */

public class Tests {
    @Mock
    private LoginViewModel lgvm;

    @Test
    public void translator() {
        assertEquals("MONDAY", Constants.getDaysTraductor().get("en").get("LUNES"));
    }
}
