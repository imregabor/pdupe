# pdupe

**This is a work in progress project.**

This tool helps to sort my photo collection.

    ./build/install/pdupe/bin/pdupe add -o pix-sorted.bin -id ".*-tmb_\\d*0" -if ".*Thumbs.db" f:/___FOTO_PRIMARY z:/__FOTO__PRIMARY__
    ./build/install/pdupe/bin/pdupe add -o pix-unsorted.bin -id ".*-tmb_\\d*0" -if ".*Thumbs.db" z:/__FOTO-RAW__PRIMARY__
    ./build/install/pdupe/bin/pdupe find -a size -oq pix-unsorted-candidates.txt -ot pix-sorted-candidates.txt -q pix-unsorted.bin -t pix-sorted.bin
    ./build/install/pdupe/bin/pdupe calcsum -i pix-sorted-candidates.txt -m SHA512 -o pix-sorted-sha512.txt
    ./build/install/pdupe/bin/pdupe mergesum -bi pix-sorted.bin -bo pix-sorted-2.bin -ci pix-sorted-sha512.txt


