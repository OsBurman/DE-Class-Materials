import { useState } from 'react'

const POSITIONS = ['Frontend Developer', 'Backend Developer', 'Full-Stack Developer', 'DevOps Engineer', 'QA Engineer']
const EXPERIENCE_LEVELS = ['Junior (0–2 years)', 'Mid-level (3–5 years)', 'Senior (6+ years)']
const SKILLS_LIST = ['JavaScript', 'TypeScript', 'React', 'Node.js', 'Python']

// TODO 1: Create a `INITIAL_FORM` constant — an object with these keys set to empty values:
//   fullName: '', email: '', phone: '', position: '', experienceLevel: '',
//   skills: [], coverLetter: '', portfolioUrl: '', agreeToTerms: false

// TODO 2: Add two pieces of state inside the component:
//   - `formData` (initialised from INITIAL_FORM) — holds all form field values
//   - `errors`   (initialised as {})           — holds validation error messages

// TODO 3: Implement `handleChange(e)`:
//   Destructure name, value, type, and checked from e.target.
//   - For type 'checkbox' with name 'agreeToTerms': toggle the boolean value.
//   - For type 'checkbox' with name 'skills': add/remove the value from the skills array.
//   - For everything else: set formData[name] = value.
//   Always clear the error for the changed field: setErrors(prev => ({ ...prev, [name]: '' }))

// TODO 4: Implement `validate()`:
//   Returns an errors object. Add an error message string for each failing rule:
//   - fullName: required
//   - email: required + must match /^[^\s@]+@[^\s@]+\.[^\s@]+$/
//   - phone: if provided, must match /^\d{10,}$/
//   - position: required
//   - experienceLevel: required
//   - skills: array must have at least 1 item
//   - coverLetter: required + at least 50 characters
//   - agreeToTerms: must be true
//   Return the object (empty object means no errors).

// TODO 5: Implement `handleSubmit(e)`:
//   - Prevent default form submission.
//   - Call validate() and store the result.
//   - If there are any errors (Object.keys(newErrors).length > 0), call setErrors(newErrors) and return.
//   - Otherwise call props.onSubmit(formData).

function ApplicationForm({ onSubmit }) {
  // TODO: Replace these placeholders with real state (see TODOs 1–2 above)
  const formData = {}
  const errors = {}

  return (
    <form className="app-form" onSubmit={() => {}}>
      <h2>Application Form</h2>

      {/* ── Personal Information ── */}
      <fieldset>
        <legend>Personal Information</legend>

        <div className="form-group">
          <label htmlFor="fullName">Full Name *</label>
          {/* TODO 6: Add value={formData.fullName} and onChange={handleChange} to this input */}
          <input type="text" id="fullName" name="fullName" placeholder="Jane Smith" />
          {/* TODO 7: Conditionally render an error message: {errors.fullName && <span className="error">{errors.fullName}</span>} */}
        </div>

        <div className="form-group">
          <label htmlFor="email">Email Address *</label>
          {/* TODO 8: Bind value and onChange; show errors.email */}
          <input type="email" id="email" name="email" placeholder="jane@example.com" />
          {errors.email && <span className="error">{errors.email}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="phone">Phone Number</label>
          {/* TODO 9: Bind value and onChange; show errors.phone */}
          <input type="tel" id="phone" name="phone" placeholder="5551234567 (optional)" />
          {errors.phone && <span className="error">{errors.phone}</span>}
        </div>
      </fieldset>

      {/* ── Position Details ── */}
      <fieldset>
        <legend>Position Details</legend>

        <div className="form-group">
          <label htmlFor="position">Position Applying For *</label>
          {/* TODO 10: Bind value and onChange on the <select>; show errors.position */}
          <select id="position" name="position">
            <option value="">— Select a position —</option>
            {POSITIONS.map(p => <option key={p} value={p}>{p}</option>)}
          </select>
          {errors.position && <span className="error">{errors.position}</span>}
        </div>

        <div className="form-group">
          <label>Experience Level *</label>
          {/* TODO 11: For each EXPERIENCE_LEVELS item render a radio input with:
                  type="radio" name="experienceLevel" value={level}
                  checked={formData.experienceLevel === level} onChange={handleChange}
              Show errors.experienceLevel below the group. */}
          <div className="radio-group">
            {EXPERIENCE_LEVELS.map(level => (
              <label key={level} className="radio-label">
                <input type="radio" name="experienceLevel" value={level} /> {level}
              </label>
            ))}
          </div>
          {errors.experienceLevel && <span className="error">{errors.experienceLevel}</span>}
        </div>

        <div className="form-group">
          <label>Skills (select all that apply) *</label>
          {/* TODO 12: For each SKILLS_LIST item render a checkbox with:
                  type="checkbox" name="skills" value={skill}
                  checked={formData.skills.includes(skill)} onChange={handleChange}
              Show errors.skills below the group. */}
          <div className="checkbox-group">
            {SKILLS_LIST.map(skill => (
              <label key={skill} className="checkbox-label">
                <input type="checkbox" name="skills" value={skill} /> {skill}
              </label>
            ))}
          </div>
          {errors.skills && <span className="error">{errors.skills}</span>}
        </div>
      </fieldset>

      {/* ── Application Materials ── */}
      <fieldset>
        <legend>Application Materials</legend>

        <div className="form-group">
          <label htmlFor="coverLetter">Cover Letter * (min 50 characters)</label>
          {/* TODO 13: Bind value and onChange; show errors.coverLetter and a live char count */}
          <textarea id="coverLetter" name="coverLetter" rows={6}
            placeholder="Tell us why you're a great fit for this role..." />
          {errors.coverLetter && <span className="error">{errors.coverLetter}</span>}
          {/* TODO 14: Show a character count like: <span className="char-count">0 / 50 minimum</span> */}
        </div>

        <div className="form-group">
          <label htmlFor="portfolioUrl">Portfolio URL (optional)</label>
          <input type="url" id="portfolioUrl" name="portfolioUrl" placeholder="https://yourportfolio.dev" />
        </div>
      </fieldset>

      {/* ── Agreement ── */}
      <div className="form-group checkbox-terms">
        {/* TODO 15: Bind checked={formData.agreeToTerms} and onChange={handleChange};
              show errors.agreeToTerms */}
        <label className="checkbox-label">
          <input type="checkbox" name="agreeToTerms" />
          I agree to the terms and conditions *
        </label>
        {errors.agreeToTerms && <span className="error">{errors.agreeToTerms}</span>}
      </div>

      <button type="submit" className="submit-btn">Submit Application</button>
    </form>
  )
}

export default ApplicationForm
