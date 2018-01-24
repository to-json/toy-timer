# pomodoro

a cute pomodoro timer

## Overview

one day i was looking for a pomo timer bc i needed one but they were
all very *severe* and i wanted something maybe more _soft_ to be in
my peripheral vision all day so i made something and here it is.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

### Wait but which one do i care about

i just use

    lein figwheel

because it's a toy and i want to be able to hack on it at will.

### but what the christ is lein?

lein is the clojure build tool. it's probably in your package manager of choice

if not i can't really help you rn sorry. better docs later maybe.

## License

Copyright © 2017 Jason Saxon

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
