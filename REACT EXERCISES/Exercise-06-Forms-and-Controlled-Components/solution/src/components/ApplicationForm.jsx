import { useState } from 'react'

const POSITIONS = ['Frontend Developer', 'Backend Developer', 'Full-Stack Developer', 'DevOps Engineer', 'QA Engineer']
const EXPERIENCE_LEVELS = ['Junior (0–2 years)', 'Mid-level (3–5 years)', 'Senior (6+ years)']
const SKILLS_LIST = ['JavaScript', 'TypeScript', 'React', 'Node.js', 'Python']

const INITIAL_FORM = {
  fullName: '', email: '', phone: '', position: '', experienceLevel: '',
  skills: [], coverLetter: '', portfolioUrl: '', agreeToTerms: false,
}

function validate(formData) {
  const errors = {}
  if (!formData.fullName.trim()) errors.fullName = 'Full name is required.'
  if (!formData.email.trim()) {
    errors.email = 'Email is required.'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = 'Please enter a valid email address.'
  }
  if (formData.phone && !/^\d{10,}$/.test(formData.phone.replace(/\D/g, ''))) {
    errors.phone = 'Phone number must be at least 10 digits.'
  }
  if (!formData.position) errors.position = 'Please select a position.'
  if (!formData.experienceLevel) errors.experienceLevel = 'Please select your experience level.'
  if (formData.skills.length === 0) errors.skills = 'Please select at least one skill.'
  if (!formData.coverLetter.trim()) {
    errors.coverLetter = 'Cover letter is required.'
  } else if (formData.coverLetter.trim().length < 50) {
    errors.coverLetter = `Cover letter must be at least 50 characters (currently ${formData.coverLetter.trim().length}).`
  }
  if (!formData.agreeToTerms) errors.agreeToTerms = 'You must agree to the terms and conditions.'
  return errors
}

function ApplicationForm({ onSubmit }) {
  const [formData, setFormData] = useState(INITIAL_FORM)
  const [errors, setErrors] = useState({})

  function handleChange(e) {
    const { name, value, type, checked } = e.target
    let newValue
    if (type === 'checkbox' && name === 'skills') {
      newValue = checked
        ? [...formData.skills, value]
        : formData.skills.filter(s => s !== value)
    } else if (type === 'checkbox') {
      newValue = checked
    } else {
      newValue = value
    }
    setFormData(prev => ({ ...prev, [name]: newValue }))
    setErrors(prev => ({ ...prev, [name]: '' }))
  }

  function handleSubmit(e) {
    e.preventDefault()
    const newErrors = validate(formData)
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }
    onSubmit(formData)
  }

  return (
    <form className="app-form" onSubmit={handleSubmit} noValidate>
      <h2>Application Form</h2>

      <fieldset>
        <legend>Personal Information</legend>

        <div className="form-group">
          <label htmlFor="fullName">Full Name *</label>
          <input type="text" id="fullName" name="fullName"
            value={formData.fullName} onChange={handleChange}
            placeholder="Jane Smith"
            className={errors.fullName ? 'input-error' : ''} />
          {errors.fullName && <span className="error">{errors.fullName}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="email">Email Address *</label>
          <input type="email" id="email" name="email"
            value={formData.email} onChange={handleChange}
            placeholder="jane@example.com"
            className={errors.email ? 'input-error' : ''} />
          {errors.email && <span className="error">{errors.email}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="phone">Phone Number</label>
          <input type="tel" id="phone" name="phone"
            value={formData.phone} onChange={handleChange}
            placeholder="5551234567 (optional)"
            className={errors.phone ? 'input-error' : ''} />
          {errors.phone && <span className="error">{errors.phone}</span>}
        </div>
      </fieldset>

      <fieldset>
        <legend>Position Details</legend>

        <div className="form-group">
          <label htmlFor="position">Position Applying For *</label>
          <select id="position" name="position"
            value={formData.position} onChange={handleChange}
            className={errors.position ? 'input-error' : ''}>
            <option value="">— Select a position —</option>
            {POSITIONS.map(p => <option key={p} value={p}>{p}</option>)}
          </select>
          {errors.position && <span className="error">{errors.position}</span>}
        </div>

        <div className="form-group">
          <label>Experience Level *</label>
          <div className="radio-group">
            {EXPERIENCE_LEVELS.map(level => (
              <label key={level} className="radio-label">
                <input type="radio" name="experienceLevel" value={level}
                  checked={formData.experienceLevel === level}
                  onChange={handleChange} />
                {level}
              </label>
            ))}
          </div>
          {errors.experienceLevel && <span className="error">{errors.experienceLevel}</span>}
        </div>

        <div className="form-group">
          <label>Skills (select all that apply) *</label>
          <div className="checkbox-group">
            {SKILLS_LIST.map(skill => (
              <label key={skill} className="checkbox-label">
                <input type="checkbox" name="skills" value={skill}
                  checked={formData.skills.includes(skill)}
                  onChange={handleChange} />
                {skill}
              </label>
            ))}
          </div>
          {errors.skills && <span className="error">{errors.skills}</span>}
        </div>
      </fieldset>

      <fieldset>
        <legend>Application Materials</legend>

        <div className="form-group">
          <label htmlFor="coverLetter">Cover Letter * (min 50 characters)</label>
          <textarea id="coverLetter" name="coverLetter" rows={6}
            value={formData.coverLetter} onChange={handleChange}
            placeholder="Tell us why you're a great fit for this role..."
            className={errors.coverLetter ? 'input-error' : ''} />
          {errors.coverLetter && <span className="error">{errors.coverLetter}</span>}
          <span className="char-count">{formData.coverLetter.length} / 50 minimum</span>
        </div>

        <div className="form-group">
          <label htmlFor="portfolioUrl">Portfolio URL (optional)</label>
          <input type="url" id="portfolioUrl" name="portfolioUrl"
            value={formData.portfolioUrl} onChange={handleChange}
            placeholder="https://yourportfolio.dev" />
        </div>
      </fieldset>

      <div className="form-group checkbox-terms">
        <label className="checkbox-label">
          <input type="checkbox" name="agreeToTerms"
            checked={formData.agreeToTerms} onChange={handleChange} />
          I agree to the terms and conditions *
        </label>
        {errors.agreeToTerms && <span className="error">{errors.agreeToTerms}</span>}
      </div>

      <button type="submit" className="submit-btn">Submit Application</button>
    </form>
  )
}

export default ApplicationForm
