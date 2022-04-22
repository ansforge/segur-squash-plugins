/**
 * ====
 *         This file is part of the Squashtest platform.
 *         Copyright (C) 2010 - 2015 Henix, henix.fr
 *
 *         See the NOTICE file distributed with this work for additional
 *         information regarding copyright ownership.
 *
 *         This is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Lesser General Public License as published by
 *         the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         this software is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Lesser General Public License for more details.
 *
 *         You should have received a copy of the GNU Lesser General Public License
 *         along with this software.  If not, see <http://www.gnu.org/licenses/>.
 * ====
 *
 *     This file is part of the Squashtest platform.
 *     Copyright (C) 2010 - 2021 Henix, henix.fr
 *
 *     See the NOTICE file distributed with this work for additional
 *     information regarding copyright ownership.
 *
 *     This is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     this software is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.squashtest.tm.plugin.custom.report.segur.repository.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.squashtest.tm.plugin.custom.report.segur.model.CampaignDto;
import org.squashtest.tm.plugin.custom.report.segur.repository.CampaignCollector;

import javax.inject.Inject;

import static org.squashtest.tm.jooq.domain.Tables.CAMPAIGN;
import static org.squashtest.tm.jooq.domain.Tables.CAMPAIGN_LIBRARY_NODE;


@Repository
public class CampaignCollectorImpl implements CampaignCollector {

    @Inject
    private DSLContext dsl;

    @Override
    public CampaignDto findCampaignById(Long campaignId) {
        return dsl.select(CAMPAIGN_LIBRARY_NODE.NAME, CAMPAIGN_LIBRARY_NODE.DESCRIPTION, CAMPAIGN.REFERENCE, CAMPAIGN_LIBRARY_NODE.CREATED_BY)
                .from(CAMPAIGN_LIBRARY_NODE)
                .leftJoin(CAMPAIGN).on(CAMPAIGN.CLN_ID.eq(CAMPAIGN_LIBRARY_NODE.CLN_ID))
                .where(CAMPAIGN_LIBRARY_NODE.CLN_ID.eq(campaignId))
                .fetchOneInto(CampaignDto.class);
    }
}
