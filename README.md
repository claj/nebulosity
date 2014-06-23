# nebulosity

clojure code surrounded by nebolsity

## Educational

Aiming to be a small web application, with machine learning methods for selecting
the best possible tasks for the student. It could be seen as a min-max-tree selection
where one hopefully could learn a bit about other students, and hopefully even
use students similar to this one to get the best prognostisation.

currently it runs an in-memory datomic database for everything.

start application with

      lein run

which starts the educational.main application.

now browse to http://localhost:8080/


start ClojureScript compilation with

      lein cljsbuild auto

run tests with

      lein midje educational.webifc-test 

but the :autotest option is somehow broken.

## Theoretical background (sketchy) <a id="theory" />

### Constraints
We don't have enough tasks in the system to actually get the user from her currect knowledge to the goal

We may put up meaningless goals

The user has limited patience with a dubious system

The user has limited ability to grasp everything in

No gain can be made from different users - they are simply to different

The user forgets (see SuperMemo algorithms)

The user already know things in a suprising way

The system has limited insight in which tasks leading forward

The users mood varies between various sessions (tired, sick, upset etc)



### About knowledge


### Goal(s)
When a user answers correctly on "Goal questions", the user knows the stuff. Of course the user can forget, but at that particular time we can be sure the user know the things.

### Hypothesis: Optimal ordering
There's an optimal ordering on which tasks that should be presented when.

This is slightly similar to SuperMemo, but for learning new things as well.

There's a concept of a usage session which is simply

    [task1 task2 task3 ...]

which ideally should lead the user closer to the goal.


### Strategy
It seems to be hard to combine strategies with other optimizations, maybe it's the balancing between, for instance, searching for new information while trying to figure out rules from previous information.

In the case of the sessions, we need to be able to both gather information about the users knowledge, as well as maintaining a good collection of tasks, both easy and hard, and with some kind of coherent line in between them. 

A hard requirement is that a task selector algorithm should chose suitable tasks with regard to the global goals, not local goals (this is to avoid getting captured in local maximas)

### A potentially good model:

A task has a success-vector and a fail-vector.

When a user attempts to solve a task, the outcome of that trial makes us add the users "contextual vector" to the success-vector on success, and fail-vector on failure.

By comparing a second users context vector to the success- and fail-vectors we can get an estimate on whether the user will succeed or fail the task.

A nice side-effect is that the vector difference for a tasks success- and failvectors will give us an estimate on which tasks would be regarded as nescessary before-hand-knowledge.

### API

(compare task-x task-y user)

(preparation task-x task-y user)

(cose task-from task-to)

(risk task-x)


### Further thoughts
Would it be possible to create some algebra for strategies for traversing the graph of tasks?

Is there any good way to make the graph more scale-free, for instance for heuristics from one task to another.

Do destructive knowledge exist? This would be tasks or knowledge that actually made it harder for the user to get further. Maybe things like wrong abstractions, or a certain view of a subject as terribly complicated would sort here.

## License

Copyright (c) Linus Ericsson. All rights reserved.

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 which can be found in the file epl-v10.html
at the root of this distribution. By using this software in any fashion,
you are agreeing to be bound by the terms of this license.

You must not remove this notice, or any other, from this software.