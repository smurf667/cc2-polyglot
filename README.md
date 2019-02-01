# Coding Challenge II: Polyglot

This is the `solutions` branch with example solutions to the coding challenge. You can also switch to the [`master`](../../) branch.

This branch includes a slide reporter which gives a nice HTML-based slide deck with the results.
You can generate it by issuing

	mvn -Prun verify -DrunArgs="-reporter=slides -inputs=src/main/resources/challenges.json"

The result can be found at `target/slides/index.html`. I hosting a demonstration online version at https://www.engehausen.de/jan/cc2/index.html

I'm the "codemonkey" contributor, feel free to explore what I did. Running this at my company showed that some challenges have better performing and more elegantly formulatable solutions.
