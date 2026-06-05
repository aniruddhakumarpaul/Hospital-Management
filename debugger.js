const puppeteer = require('puppeteer');
const wait = (ms) => new Promise(r => setTimeout(r, ms));

(async () => {
    console.log("Launching Puppeteer Debugger...");
    const browser = await puppeteer.launch({ headless: 'new' });
    const page = await browser.newPage();
    
    page.on('console', msg => console.log(`[BROWSER CONSOLE] ${msg.type().toUpperCase()}: ${msg.text()}`));
    page.on('pageerror', err => console.log(`[BROWSER ERROR]: ${err.toString()}`));
    page.on('requestfailed', request => console.log(`[NETWORK ERROR]: ${request.url()} - ${request.failure()?.errorText}`));

    console.log("Navigating to http://localhost:9000/terminal.html ...");
    await page.goto('http://localhost:9000/terminal.html', { waitUntil: 'load' });
    await wait(1000);

    // TEST AREA 1: Generate Medical Token (Non-Emergency)
    console.log("\n--- TEST: GENERATE MEDICAL TOKEN ---");
    try {
        await page.type('#name', 'John Smith');
        await page.type('#age', '35');
        await page.type('#phone', '1234567890');
        // Will map to Gastroenterologist
        await page.type('#illness', 'My stomach hurts and digestion is bad.');
        await wait(1000); 
        
        console.log("Clicking 'Apply' AI Suggestion button...");
        await page.click('#aiSuggestionBox button');
        await wait(500);
        
        console.log("Clicking 'Generate Medical Token' button...");
        await page.click('button[type="submit"]');
        await wait(2000); // Wait for API
    } catch(e) {
        console.error("Test 1 Failed to execute:", e);
    }

    // TEST AREA 2: Find Record
    console.log("\n--- TEST: FIND RECORD ---");
    try {
        await page.click('button#nav-search-tab');
        await wait(500);
        await page.type('#searchId', '1234567890');
        await page.click('button[onclick="searchPatient()"]');
        await wait(2000); // Wait for API
    } catch(e) {
        console.error("Test 2 Failed to execute:", e);
    }

    console.log("\nTests Complete. Closing browser...");
    await browser.close();
})();
