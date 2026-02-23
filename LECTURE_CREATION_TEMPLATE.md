# Lecture Creation Prompt - Reusable Template

Use this prompt for creating lectures for any day. Copy, paste, and fill in the bracketed information for each new day.

---

## PROMPT TO COPILOT:

I am teaching a class on software engineering and each day will consist of two lectures, each one hour long. I have created folders for each day. Each day has topics separated into two parts. I want you to create two hour long lectures with slides that I can say to the class. I want to have both the slides and the verbatim script for it. 

Each week/day folder should have its own Slides Folder and in the Slides folder, there should be a folder called Part 1 with the slide descriptions and slides for part 1 and another folder called Part 2 with the slides descriptions and slides script for part 2. 

When you're done creating it, I want you to review what you've created and see if there's anything I'm missing that a person learning about these topics and this subject should know or anything excessive/extra in it. When you're doing that, there is a half-day-syllabus.txt file where you can see upcoming lessons and that way you won't add anything we are covering in previous or next lessons. If you think there is something we should add or take out in the slides you just created, don't change anything just mention it to me. 

Let's do the following week and day now with these topics:

### [WEEK AND DAY]: [TITLE]

**Part 1:**
- [TOPIC 1 - PART 1]
- [TOPIC 2 - PART 1]
- [TOPIC 3 - PART 1]

**Part 2:**
- [TOPIC 1 - PART 2]
- [TOPIC 2 - PART 2]
- [TOPIC 3 - PART 2]

**Learning Objectives:**
- [OBJECTIVE 1]
- [OBJECTIVE 2]
- [OBJECTIVE 3]
- [OBJECTIVE 4]
- [OBJECTIVE 5]

---

## INSTRUCTIONS FOR YOU (THE USER):

### Step 1: Gather Information
Before using the prompt above, have ready:
- Week and Day number and title (e.g., "Week 1 - Day 2 (Tuesday)")
- Part 1 topics (3-5 main topics)
- Part 2 topics (3-5 main topics)
- Learning objectives (at least 3-5)
- Copy these directly from your half-day-syllabus.txt file

### Step 2: Fill in the Template
Replace all [BRACKETED] sections with your specific content:
- [WEEK AND DAY] = "Week 1 - Day 2 (Tuesday): Core Java Fundamentals — Part 1"
- [TITLE] = Your day's title
- [TOPIC 1 - PART 1] = First topic for part 1, etc.
- [OBJECTIVE 1] = First learning objective, etc.

### Step 3: Copy and Paste to Copilot
Copy the entire filled-in prompt and paste it into your Copilot conversation.

### Step 4: Folder Structure Created
Copilot will automatically create:
```
Week X - Day Y - [Title]/
├── Slides/
    ├── Part 1/
    │   ├── SLIDE_DESCRIPTIONS.md
    │   └── LECTURE_SCRIPT.md
    └── Part 2/
        ├── SLIDE_DESCRIPTIONS.md
        └── LECTURE_SCRIPT.md
```

### Step 5: Review the Output
Copilot will provide:
1. Complete lecture materials (slides and verbatim scripts)
2. A comprehensive review identifying:
   - What's covered well
   - Potential gaps (with suggestions)
   - Potential excesses (what could be trimmed)
   - Alignment with syllabus

### Step 6: File Contents
Each day folder will contain:

**SLIDE_DESCRIPTIONS.md** - Includes:
- Slide-by-slide visual design specs
- Content points for each slide
- Timing recommendations
- Format: Simple markdown with clear headers

**LECTURE_SCRIPT.md** - Includes:
- Complete verbatim narration for the entire 60-minute lecture
- Timing markers (*[X minutes]*)
- Instructions for what to do/ask
- Q&A session at the end
- Format: Conversational, ready to read aloud

### Step 7: Quality Checklist
After creation, verify:
- [ ] Folder structure matches pattern
- [ ] Part 1 and Part 2 are separate
- [ ] Scripts are approximately 60 minutes each (can verify by counting ~150 words = 1 minute)
- [ ] Learning objectives from syllabus are addressed
- [ ] No overlap with previous/next days' topics
- [ ] Review feedback is provided

---

## EXAMPLE - HOW TO FILL IN:

### Template (blank):
```
Let's do [WEEK AND DAY] now with these topics:

### [WEEK AND DAY]: [TITLE]

**Part 1:**
- [TOPIC 1]
- [TOPIC 2]
- [TOPIC 3]

**Part 2:**
- [TOPIC 1]
- [TOPIC 2]
- [TOPIC 3]
```

### Filled In (Example):
```
Let's do Week 1 - Day 2 now with these topics:

### Week 1 - Day 2 (Tuesday): Core Java Fundamentals — Part 1

**Part 1:**
- JVM, JRE, and JDK architecture
- Java primitives and data types
- Variables, literals, and constants
- Type conversion, casting, autoboxing, and unboxing

**Part 2:**
- Strings and String operations
- StringBuilder and StringBuffer (mutability, performance in loops)
- Mathematical, logical, and comparison operators
- Comments and code documentation

**Learning Objectives:**
- Explain the relationship between JVM, JRE, and JDK
- Declare and initialize variables with appropriate data types
- Perform type conversions and casting operations
- Work with String manipulation methods
```

---

## KEY GUIDELINES:

1. **Extract from Syllabus**: Copy topics and learning objectives directly from half-day-syllabus.txt
2. **Timing**: Each part should be ~60 minutes
3. **Script Format**: Conversational, as if you're speaking to a class
4. **Timing Markers**: Include *[X minutes]* for each segment
5. **Slide Design**: Focus on visual simplicity in descriptions (not actual graphics)
6. **Depth**: Foundational enough for beginners but with professional context
7. **Real-World**: Include practical examples and why concepts matter

---

## WHAT COPILOT WILL DELIVER:

### For Each Lecture (Part 1 & Part 2):

1. **SLIDE_DESCRIPTIONS.md**: 
   - 30-50 slides per lecture
   - Each slide has visual guidance and content points
   - Clear structure and progression

2. **LECTURE_SCRIPT.md**:
   - ~7,500-10,000 words (approximately 60 minutes when read aloud)
   - Broken into segments with timing
   - Conversational tone
   - Ready to read verbatim
   - Includes Q&A time at end

3. **Review & Feedback**:
   - What's covered well
   - Gaps in content (with suggestions)
   - Excesses/things to consider removing
   - Syllabus alignment check
   - Recommendations for enhancement

---

## AFTER EACH CREATION:

1. Review Copilot's feedback
2. Decide if you want to make adjustments
3. If yes, ask Copilot to modify specific sections
4. Once satisfied, move to next day
5. Repeat for all 40 days

---

## NOTES:

- Each lecture is self-contained; students don't need materials from other days
- Scripts can be read verbatim or used as reference—adjust as needed
- Slides descriptions provide visual specs but aren't actual PowerPoint/design files
- Consider creating supplementary materials (labs, exercises) separately if desired
- Keep consistent structure across all days for student familiarity

---

**Ready to use this template? Copy it, fill in the bracketed sections, and paste the prompt into Copilot!**
