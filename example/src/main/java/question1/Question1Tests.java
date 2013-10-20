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

public class Question1Tests extends FeedbackSuite {
        
    public Question1Tests(final Question1 ans) {
        addTest(new isLeafTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer1(null);
            } // end of method setUp
        });
        addTest(new isSingleBranchTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer1(null);
            } // end of method setUp
        });
        addTest(new isManyBranchingTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer1(null);
            } // end of method setUp
        });
        addTest(new containsTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer1(null);
            } // end of method setUp
        });
        addTest(new addSubTreeTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer1(null);
            } // end of method setUp
        });
        addTest(new deleteSubTreeTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer1(null);
            } // end of method setUp
        });
        addTest(new equalsTests() {
            public void setUp() {
                this.answer = ans;
                this.model = new ModelAnswer1(null);
            } // end of method setUp
        });
    } // end of constructor function

} // end of class Question1Tests
