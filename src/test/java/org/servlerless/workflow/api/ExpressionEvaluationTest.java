/*
 *
 *   Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.servlerless.workflow.api;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.serverless.workflow.api.Workflow;
import org.serverless.workflow.api.WorkflowController;
import org.serverless.workflow.api.states.EventState;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpressionEvaluationTest extends BaseWorkflowTest {

    @Test
    public void testEventStateExpressions() {

        Workflow workflow = toWorkflow(getFileContents(getResourcePath("expressions/eventstatestriggers.json")));

        WorkflowController controller = new WorkflowController(workflow);
        assertTrue(controller.isValid());

        assertTrue(controller.haveTriggers());
        assertTrue(controller.haveStates());

        assertThat(workflow.getTriggerDefs().size(),
                   is(2));

        assertThat(workflow.getStates().size(),
                   is(2));

        List<EventState> eventStatesForTrigger1 = controller.getEventStatesForTriggerEvent(controller.getUniqueTriggerEvents().get("2"));
        assertNotNull(eventStatesForTrigger1);
        assertEquals(2,
                     eventStatesForTrigger1.size());
        EventState eventStateForTrigger1 = eventStatesForTrigger1.get(0);
        assertEquals("4",
                     eventStateForTrigger1.getId());
        EventState eventStateForTrigger2 = eventStatesForTrigger1.get(1);
        assertEquals("5",
                     eventStateForTrigger2.getId());

        List<EventState> eventStatesForTrigger2 = controller.getEventStatesForTriggerEvent(controller.getUniqueTriggerEvents().get("3"));
        assertNotNull(eventStatesForTrigger2);
        assertEquals(1,
                     eventStatesForTrigger2.size());
        EventState eventStateForTrigger3 = eventStatesForTrigger2.get(0);
        assertEquals("5",
                     eventStateForTrigger3.getId());
    }
}
