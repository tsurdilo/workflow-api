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

package org.servlerless.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.serverless.workflow.Workflow;
import org.serverless.workflow.actions.Action;
import org.serverless.workflow.actions.Retry;
import org.serverless.workflow.branches.Branch;
import org.serverless.workflow.choices.AndChoice;
import org.serverless.workflow.choices.DefaultChoice;
import org.serverless.workflow.events.Event;
import org.serverless.workflow.events.TriggerEvent;
import org.serverless.workflow.interfaces.Choice;
import org.serverless.workflow.interfaces.State;
import org.serverless.workflow.states.DelayState;
import org.serverless.workflow.states.EndState;
import org.serverless.workflow.states.EndState.Status;
import org.serverless.workflow.states.EventState;
import org.serverless.workflow.states.OperationState;
import org.serverless.workflow.states.ParallelState;
import org.serverless.workflow.states.SwitchState;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.servlerless.workflow.util.IsEqualJSON.equalToJSONInFile;

public class WorkflowToJsonTest extends BaseWorkflowTest {

    @Test
    public void testEmptyWorkflow() {
        Workflow workflow = new Workflow();

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("emptyworkflow.json")));
    }

    @Test
    public void testSimpleWorkflowWithInfo() {
        Workflow workflow = new Workflow().withId("testuid")
                .withDescription("testdescription")
                .withName("testname")
                .withVersion("testversion")
                .withOwner("testOwner");

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("workflowwithinfo.json")));
    }

    @Test
    public void testSimpleWorkflowWithInfoAndMetadata() {
        Workflow workflow = new Workflow().withId("testuid")
                .withDescription("testdescription")
                .withName("testname")
                .withVersion("testversion")
                .withOwner("testOwner")
                .withMetadata(
                        Stream.of(new Object[][] {
                                { "key1", "value1" },
                                { "key2", "value2" },
                        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]))
                );

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("workflowwithinfoandmetadata.json")));
    }

    @Test
    public void testTrigger() {
        Workflow workflow = new Workflow().withTriggerDefs(
                Arrays.asList(
                        new TriggerEvent().withName("testtriggerevent").withEventID("testeventid")
                                .withCorrelationToken("testcorrelationtoken").withSource("testsource")
                )
        );

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("singletriggerevent.json")));
    }

    @Test
    public void testEndState() {

        Workflow workflow = new Workflow().withStates(new ArrayList<State>() {{
            add(new EndState().withStatus(Status.SUCCESS));
        }});

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("singleendstate.json")));
    }

    @Test
    public void testEventState() {

        Workflow workflow = new Workflow().withStates(new ArrayList<State>() {{
            add(new EventState().withStart(true)
                        .withEvents(Arrays.asList(
                                new Event().withEventExpression("testEventExpression").withTimeout("testTimeout")
                                        .withActionMode(Event.ActionMode.SEQUENTIAL)
                                        .withNextState("testNextState")
                                        .withActions(Arrays.asList(
                                                new Action().withFunction("testFunction")
                                                        .withTimeout(5)
                                                        .withRetry(new Retry().withMatch("testMatch").withMaxRetry(10)
                                                                           .withRetryInterval(2)
                                                                           .withNextState("testNextRetryState"))
                                        ))
                        )));
        }});

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("singlestateevent.json")));
    }

    @Test
    public void testDelayState() {
        Workflow workflow = new Workflow().withStates(new ArrayList<State>() {{
            add(new DelayState().withStart(false).withNextState("testNextState").withTimeDelay(5));
        }});

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("singledelaystate.json")));
    }

    @Test
    public void testOperationState() {
        Workflow workflow = new Workflow().withStates(new ArrayList<State>() {{
            add(new OperationState().withStart(true).withActionMode(OperationState.ActionMode.SEQUENTIAL).withNextState("testnextstate")
                        .withActions(Arrays.asList(
                                new Action().withFunction("testFunction")
                                        .withTimeout(5)
                                        .withRetry(new Retry().withMatch("testMatch").withMaxRetry(10)
                                                           .withRetryInterval(2)
                                                           .withNextState("testNextRetryState"))
                        )));
        }});

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("singleoperationstate.json")));
    }

    @Test
    public void testParallellState() {
        Workflow workflow = new Workflow().withStates(new ArrayList<State>() {{
            add(new ParallelState().withStart(true).withNextState("testnextstate")
                        .withBranches(Arrays.asList(
                                new Branch().withName("firsttestbranch").withStates(
                                        new ArrayList<State>() {{
                                            add(new OperationState().withStart(true).withActionMode(OperationState.ActionMode.SEQUENTIAL).withNextState("testnextstate")
                                                        .withActions(Arrays.asList(
                                                                new Action().withFunction("testFunction")
                                                                        .withTimeout(5)
                                                                        .withRetry(new Retry().withMatch("testMatch").withMaxRetry(10)
                                                                                           .withRetryInterval(2)
                                                                                           .withNextState("testNextRetryState"))
                                                        )));
                                        }}
                                ),
                                new Branch().withName("secondtestbranch").withStates(
                                        new ArrayList<State>() {{
                                            add(new DelayState().withStart(false).withNextState("testNextState").withTimeDelay(5));
                                        }}
                                )
                        )));
        }});

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("singleparallelstate.json")));
    }

    @Test
    public void testSwitchState() {
        Workflow workflow = new Workflow().withStates(new ArrayList<State>() {{
            add(
                    new SwitchState().withDefault("defaultteststate").withStart(false).withChoices(
                            new ArrayList<Choice>() {{
                                add(
                                        new AndChoice().withNextState("testnextstate").withAnd(
                                                Arrays.asList(
                                                        new DefaultChoice().withNextState("testnextstate")
                                                                .withOperator(DefaultChoice.Operator.EQ)
                                                                .withPath("testpath")
                                                                .withValue("testvalue")
                                                )
                                        )
                                );
                            }}
                    )
            );
        }});

        assertNotNull(toJsonString(workflow));
        assertThat(toJsonString(workflow),
                   equalToJSONInFile(getResourcePathFor("singleswitchstateAndChoice.json")));
    }
}