package com.pyramix.persistence.voucher.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherType;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.voucher.dao.VoucherSerialNumberDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class VoucherSerialNumberHibernate extends DaoHibernate implements VoucherSerialNumberDao {

	@Override
	public VoucherSerialNumber findVoucherSerialNumberById(long id) throws Exception {
		
		return (VoucherSerialNumber) super.findById(VoucherSerialNumber.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherSerialNumber> findAllVoucherSerialNumber() throws Exception {

		return super.findAll(VoucherSerialNumber.class);
	}

	@Override
	public void save(VoucherSerialNumber voucherSerialNumber) throws Exception {
		
		super.save(voucherSerialNumber);
	}

	@Override
	public void update(VoucherSerialNumber voucherSerialNumber) throws Exception {
		
		super.update(voucherSerialNumber);
	}

	@Override
	public VoucherSerialNumber findLastVoucherSerialNumberByVoucherType(VoucherType voucherType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<VoucherSerialNumber> criteriaQuery = criteriaBuilder.createQuery(VoucherSerialNumber.class);
		Root<VoucherSerialNumber> root = criteriaQuery.from(VoucherSerialNumber.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("voucherType"), voucherType));
		criteriaQuery.orderBy(
				criteriaBuilder.desc(root.get("id")));
		
		List<VoucherSerialNumber> voucherSerialNumberList =
				session.createQuery(criteriaQuery).getResultList();
		
		try {
			if (voucherSerialNumberList.isEmpty()) {
				return null;
			} else {
				return voucherSerialNumberList.get(0);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		

	}

}
