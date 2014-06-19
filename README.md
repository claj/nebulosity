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

run tests with

    lein midje educational.webifc-test 

but the :autotest option is somehow broken.



## License

Copyright (c) Linus Ericsson. All rights reserved.

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 which can be found in the file epl-v10.html
at the root of this distribution. By using this software in any fashion,
you are agreeing to be bound by the terms of this license.

You must not remove this notice, or any other, from this software.