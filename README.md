# pdupe

**This is a work in progress project.**

This tool helps to sort my photo collection.

    ./build/install/pdupe/bin/pdupe add -o a.bin -id ".*-tmb_\\d*0" -if ".*Thumbs.db" e:/FOTO
    ./build/install/pdupe/bin/pdupe add -o b.bin -id ".*-tmb_\\d*0" -if ".*Thumbs.db" e:/FOTO-TOSORT
    ./build/install/pdupe/bin/pdupe find -q b.bin -t a.bin -oq q.txt -ot t.txt

