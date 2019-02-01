# Test inputs

Thanks to the wonderful world of JSON where comments are not allowed,
this file describes the format of the test inputs.

The format is actually simply a serialization of the `de.engehausen.cc2.data.Inputs` class.
It contains for each challenge a list of inputs and expectations. Each
input should have a `label` which will be used during reporting.

All data is directly contained in the input file, except for the XML cases.
`xmlIn` points to an XML file _available on the classpath_.
