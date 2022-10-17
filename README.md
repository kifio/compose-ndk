App take .txt file from assests and looking lines matches with user regex.
Pattern matching logic has two implementations: 
 - Kotlin implementation. This logic placed in `LocalRepository.getFilteredStringsKotlin` method. 
Just a few lines of Kotlin is enough to read, filter and collect all lines. Trivial.
 - C++ implementation is much more interesting. 
All logic implemented in `stringsparser.cpp`. Principe are the same, but without Kotlin syntax sugar. 
All the convertations from `jstring` to `string` maybe are ineffective. `ArrayList` instation are manual.
Perfomance is good, but it took much more time then Kotlin implementations. At least because file reading going on without buffering.

That's just simple trying of NDK. I used it before a little bit for just encoding strings, but not for complex logic with native Android API.
Hope I would never use it seriously, but at least I would be ready.