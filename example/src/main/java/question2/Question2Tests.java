// Copyright (C) 2003-2004, 2013  Carl Pulley
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package example;

import java.util.*;

public class Question2Tests extends FeedbackSuite {
        
    public Question2Tests(final Question2 ans) {
        addTest(new convertTreeTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer2(new ModelAnswer1(null));
            } // end of method setUp
        });
        addTest(new referenceCountsTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer2(new ModelAnswer1(null));
            } // end of method setUp
        });
        addTest(new rootNodeTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer2(new ModelAnswer1(null));
            } // end of method setUp
        });
    } // end of constructor function

} // end of class Question2Tests
