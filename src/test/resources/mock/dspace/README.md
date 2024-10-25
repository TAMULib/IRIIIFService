## Mock Test Data Directory for DSpace

There are three directories present.
  1. decoded
  2. encoded
  3. uncoded

The **decoded** directory contains a decoded characters, such as a space character ` ` (U+0020).
Many tests should be expected to fail with this as some of these are invalid.

The **encoded** directory contains encoded characters, such as `%20` (U+0025 U+0032 U+0030).
Many tests should be expected to pass with these as the encoded should result in valid URLs.

The **uncoded** directory contains tests that has neither encoded character nor decoded characters.
Only normal characters are used here and `%` (U+0025) should not be present in any of the URLs.

